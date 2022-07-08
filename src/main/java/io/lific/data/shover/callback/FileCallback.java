package io.lific.data.shover.callback;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Set;

@Slf4j
public class FileCallback implements Callback {

    private final ProducerRecord<String, GenericRecord> record;
    private final String basePath;
    private static final long MAX_FILE_SIZE = 10485706; // 10Mb
    //private static final long MAX_FILE_SIZE = 1000; // test 1000byte
    private static final String DEFAULT_FILE_NAME = "schema.record";
    private static final Set<StandardOpenOption> OPTIONS = Set.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);


    public FileCallback(ProducerRecord<String, GenericRecord> record, String basePath) {
        this.record = record;
        this.basePath = basePath;
    }

    /**
     * Rolling되어 업데이트된 최대 파일의 Index + 1을 반환 합니다.
     * 이때, 한번도 Rolling이 되질 않았으면 숫자 0을 반환합니다.
     * @param directory
     */
    private int getLastIndex(String directory) {
        File[] files = new File(directory).listFiles((dir1, name) -> name.matches("^.*\\d$"));
        if(files == null || files.length == 0) {
            return 0;
        }
        return Arrays.stream(files)
                .mapToInt(t ->
                    Integer.parseInt(t.getName().substring(t.getName().lastIndexOf(".") + 1))
                ).max()
                .getAsInt() + 1;
    }

    /**
     * Rolling하기 위해 기존의 파일을 index 파일로 치환하고 재 생성합니다.
     * @param source 현재 파일
     * @param index 변환될 index 순서
     */
    private FileChannel rename(String source, int index) throws IOException {
        String destination = source + "." + index;
        if(new File(source).renameTo(new File(destination))) {
            log.info("schema.record rolling ::: {} -> {}", source, destination);
            return FileChannel.open(Paths.get(source), OPTIONS);
        }
        return null;
    }

    /**
     * 대상 파일을 Open 합니다. 용량 확인 후, Rolling 프로세스를 처리 합니다.
     * @param directory
     */
    private FileChannel openChannel(String directory) throws IOException {
        String file = directory + File.separator + DEFAULT_FILE_NAME;
        FileChannel channel = FileChannel.open(Paths.get(file), OPTIONS);
        if(channel.size() >= MAX_FILE_SIZE) {
            channel.close();
            return rename(file, getLastIndex(directory));
        }
        return channel;
    }

    /**
     * 해당 위치( basePath + topic )에 폴더가 없으면 생성합니다.
     * @param directory
     */
    private String directoryCheck(String directory) throws IOException {
        Path path = Paths.get(directory);
        if(Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return directory;
    }

    /**
     * 카프카 프로듀서의 전송 성공/실패 여부에 대한 프로세스를 입니다.
     * 해당 프로세스에서는 전송 실패시, 파일에 저장합니다.
     * 데이터 적재 시, Void 형태의 CompletableFuture 방식을 사용하므로 예외 발생시 알 수 가 없습니다.
     * 또한, @Override 되어지는 메소드 이므로 @SneakyThrows처리로 예외처리를 하지 않습니다.
     * @param metadata
     */
    @Override
    @SneakyThrows
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if(exception != null) {
            String directory = directoryCheck(this.basePath + File.separator + metadata.topic());
            try(FileChannel channel = openChannel(directory)) {
                channel.write(ByteBuffer.wrap((record.value().toString() + System.getProperty("line.separator")).getBytes(StandardCharsets.UTF_8)));
            }
            exception.printStackTrace();
        }
        //else {
        //    log.debug("RecordData ::: " + record);
        //}
    }
}

shover
================

카프카의 특정 토픽에 schema-registry를 거쳐 Producer의 역할을 하게 하는 라이브러리.  
해당 프로젝트의 주요 클래스는 다음과 같다.

- [build.gradle](#build.gradle)
- [Config.java](#Config)
- [SchemaCache.java](#SchemaCache)
- [SchemaModel.java](#SchemaModel)
- [ShoverComponent.java](#ShoverComponent)
- [FileCallback.java](#FileCallback)

build.gradle
================

해당 라이브러리는 gradle 버전 7.1.0로 작성  
해당 gradle 스크립트 작성시, 라이브러리 형식에 맞게 다른 프로젝트와의 의존성 충돌을 일으키지 않기 위해 최소한의 필수 기능만을 의존하도록 한다.  
예를들어 `로그백`, `스프링` 같은 의존은 넣지 않도록 설계하며 다른 스프링 프로젝트에 의존될시 같이 동작이 될 수 있도록 한다 

- 프로젝트를 jar 형태로 배포하기 위해  `java`, `java-library` 플러그인을 추가한다.
- Lific nexus에 배포하기 위해 `maven-publish` 플러그인을 추가한다.

```groovy
plugins {
    id 'java'
    id 'maven-publish'
    id 'java-library'
}
```

> gradle 7버전의 형태에서는 기존의 uploadArchieve를 사용하지 않고, publish를 사용한다.

schema-registry에 관련된 라이브러리들을 사용하기 위해선, `colfluent`에서 제공하는 라이브러리가 필요  
의존성 주입을 위해 maven repositories에 추가되어야 하고, 실제 결합되는 타 프로젝트에서도 해당 url이 추가되어야 한다.
```groovy
repositories {
    mavenCentral()
    maven {
        url "https://packages.confluent.io/maven"
    }
}
```

> confluent의 라이브러리를 직접 내려받아 `nexus에 upload하여 사용`해 보았다. 결합되는 프로젝트에 추가작업을 하게 하고 싶지 않아서인데
> 제대로 `동작을 하지 못하였다`. (각 라이브러리에 필요한 의존들이 많아서 하나하나 찾을 수가 없다)

Config
================

Spring을 사용하는 프로젝트라면 `@Configuration`을 통한 `@Bean`등록을 설정하고,  
사용하지 않는 프로젝트라면 먼저 객체를 생성하여야 한다. 해당 객체에는 다음과 같은 정보를 가지고 있다.
- Kafka Bootstrap 서버 정보 [`List`]
- Kafka Client ID 정보 [`String`]
- Kafka Producer Key Serializer Class 정보 [`String`, Default `StringSerializer.class.getName()`]
- Kafka Producer Key Serializer Class 정보 [`String`, Default `KafkaAvroSerializer.class.getName()`]
- Schema Registry URL 정보 [`String`]
- Send Miss Count 설정 [`Integer`]

> 해당 클래스는 체이닝 메소드를 사용하기 위한 `빌더 패턴`으로 설계하였다.  
> 최종 반환값은 객체값이 아닌 내부의 `Properties` 변수 이므로 `Lombok`을 사용하지 않고 직접 구현하였다.  

SchemaCache
================

Schema Registry UI를 통해 저장하거나 변경한 목록을 담고있는 객체이다.  
객체는 다음과 같은 `Map`형태로 저장한다. 또한 저장시, `가장 마지막 버전`만을 저장한다

```java
// Map<[Subject 이름], Pair<[Subject 버전], [Subject 스키마]>
private final Map<String, Pair<Integer, Schema>> subjectCache = new HashMap<>();
```

> 초기 설계에는 모든 버전의 스키마 정보를 저장하려고 하였으나, 변경시에 무조건적으로 버전이 +1이 되는 스키마 레지스트리에서는 최종 버전을 타겟팅하는것이 옳다고 생각된다.  
> 다만, 실제 `Flush`가 될 때 어떠한 버전으로 프로듀싱되고 있는지를 표현하기 위해 현재 적용중인 버전명은 필요하다고 판단된다.  
> 즉, `사용의 목적이 아닌 노출의 목적`으로 Pair 형태로 버전을 가지고 있다. 

SchemaModel
================

타 프로젝트에서 프로듀싱할 경우, `SchemaModel`을 통해서 객체를 전달한다.  
해당 클래스는 빌더 패턴으로 설계되었으며 `@SuperBuilder`처럼 Builder 체이닝을 이어갈 수 있도록 설계하였다.  
현재 버전은 단순 Key:Value의 형태만을 저장하며, 추후 필요에 의해 List나 Child Node형태도 추가하도록 한다.  
Builder를 선언시에는 `초기화`하고 build() 메소드 호출 전까지는 계속 이전의 객체 내용을 보존하도록 한다. 

[Schema Model Code](https://github.com/lific-tech/shover/blob/feature/test/src/main/java/io/lific/data/shover/schema/SchemaModel.java)

ShoverComponent
================

카프카 Topic에 전송을 하는 클래스이다. 타 프로젝트에서는 해당 객체에서 `send` 메소드를 호출하여 전송한다.  
기본적으로 return값을 반환하지 않는 `CompletableFuture<Void>`의 형태로 비동기 방식을 사용한다.  
해당 객체는 jackson의 `ObjectNode`만을 허용하므로, 직접적으로 타 프로젝트 내에서 필요 객체를 생성하거나, `SchemaModel`을 활용한다.  
또한 send 시, schema-registry와 비교하여 값을 부여한다.  

> 해당 라이브러리의 전송 정책 레벨은 `FULL`로 설계하였다. 실제로 스키마 레지스트리는 형식이 맞지 않는다고 예외를 내거나 전송을 하지 않는 기능이 아니다.  
> 해서 지정된 룰은 스키마 레지스트리에 대한 변경은  
> `[스키마 레지스트리]` -> `[프로듀서]` -> `[컨슈머]`의 순서이다.  
> 값이 변경되어 필드값이 다를 경우, 디폴트 값이 설정되어 넘어가거나 Null인채로 넘어가야 한다. (예외가 발생하여 넘어가지 않으면 안된다.)


>설정에 지정된 miss.count의 개수가 일정 수준에 도달하면 로깅을 한다. 해당 부분은 추후 slack메세지나 관리자가 알도록 고민해보는것이 좋을 듯하다.

FileCallBack
================

카프카 Producer 전송 시, Void 이므로 예외가 발생해도 해당 이슈를 알 수가 없다. 그로 인해 데이터가 유실 될 가능성이 있는데 해당 가능성을 줄이고자 특정 위치에 데이터를 적재하는 로직을 추가한다.  
데이터 적재시 s3로 마운트 하여 공통으로 접근이 가능할 수 있는 영역으로 잡는다.  
추후, 해당 데이터가 이미 schema-registry에 통과 될 수 있는 데이터라면, 강제로 hdfs에 넣는 방식이 필요할 수도 있다.

> 파일의 저장은 `base / topic / schema.record` 고정이다.  
> 데이터가 많아 질 경우, 하나의 파일에 적재되는 용량이 커지므로 `Rolling` 처리를 한다. (현재 적용값 10MB)    
> 롤링 규칙은 `schema.record.${number}`의 규칙이 적용 된다.  


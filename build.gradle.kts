    import com.amazonaws.services.lambda.model.InvocationType
    import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
    import jp.classmethod.aws.gradle.lambda.AWSLambdaInvokeTask
    import jp.classmethod.aws.gradle.lambda.AWSLambdaMigrateFunctionTask
    import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

    val coroutinesVersion = "1.3.2"

    plugins {
        id("org.springframework.boot") version "2.2.0.M6"
        id("io.spring.dependency-management") version "1.0.8.RELEASE"
        id("com.github.johnrengelman.shadow") version "2.0.4"
        id("jp.classmethod.aws") version "0.37"
        id("jp.classmethod.aws.lambda") version "0.37"
        kotlin("jvm") version "1.3.50"
        kotlin("plugin.spring") version "1.3.50"
    }

    group = "com.example"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    val developmentOnly by configurations.creating
    configurations {
        runtimeClasspath {
            extendsFrom(developmentOnly)
        }
    }

    extra["springCloudVersion"] = "Greenwich.SR3"

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        implementation("org.springframework.cloud:spring-cloud-starter-function-webflux")
        implementation("org.springframework.cloud:spring-cloud-function-adapter-aws")
        implementation("org.springframework.cloud:spring-cloud-starter-function-webflux")

        compileOnly("com.amazonaws:aws-lambda-java-events:2.0.2")
        compileOnly("com.amazonaws:aws-lambda-java-core:1.1.0")
        compileOnly("com.amazonaws:aws-java-sdk-s3:1.11.557")

        developmentOnly("org.springframework.boot:spring-boot-devtools")

        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
        testImplementation("io.projectreactor:reactor-test")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    tasks {

        named<ShadowJar>("shadowJar") {
            archiveBaseName.set("shadow")
            mergeServiceFiles()
            manifest {
                attributes(mapOf("Main-Class" to "com.example.lambda.Handler"))
            }
        }

        build {
            dependsOn(shadowJar)
        }

        withType<Test> {
            useJUnitPlatform()
        }

        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "1.8"
            }
        }

        register<AWSLambdaMigrateFunctionTask>("deployHello") {
            println("Deploying hello lambda...")
            functionName = "hello"
            runtime = com.amazonaws.services.lambda.model.Runtime.Java8
            role = "arn:aws:iam::830473435438:role/service-role/aws-kotlin-demo-role-yd2027qm"
            zipFile = shadowJar.get().archivePath
            handler = "com.example.lambda.Handler"
            memorySize = 512
            timeout = 60
            environment = mapOf(
                    "SPRING_PROFILES_ACTIVE" to "dev,aws",
                    "MAIN_CLASS" to "com.example.lambda.AwsLambdaDemoApplication")
        }

        register<AWSLambdaInvokeTask>("hello") {
            println("Invoking hello lambda...")
            functionName = "hello"
            invocationType = InvocationType.RequestResponse
            payload = "\"World!\""
            doLast {
                println("Lambda function result: ${String(invokeResult.payload.array())}")
            }
        }
    }

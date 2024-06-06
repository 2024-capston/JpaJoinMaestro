## Feature 
- Fully integrated library with JPA (Hibernates) and QueryDsl
- Focus on business logic without worrying about Optimize join performance and DTO-entity mapping
- Finds the index of the Entity on itself and rearranges the predicates to create the optimal query.  
- Find associations between entities on your own and perform the Join operation

## Installation 
1. Download deploy/* in repository
2. Update your build.gradle as following 
``` java
repositories {
	mavenCentral()
	maven {
    ...
		url = uri("file:///absolute/path/to/deploy")
	}

  dependencies {
    ...
    implementation 'org.sejong:JpajoinMaestro:0.0.1-SNAPSHOT'
  }
}
```

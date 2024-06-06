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
  // add maven 
  maven {
    url = uri("file:///absolute/path/to/deploy")
  }

  dependencies {
    // add dependency
    implementation 'org.sejong:JpajoinMaestro:0.0.1-SNAPSHOT'
  }
}
```

## How to use 
### 1. Add the `@DTOMapping` Annotation to your DTO.

There are two attributes to include
domain: Defines from which @Entity Class the respective DTO Field is sourced.
field: Specifies which Field from the @Entity Class corresponds to the respective DTO Field.

An example of how to use this is as follows:
``` java 
package org.sejong.jpajoinmaestro.dto;

import lombok.Getter;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.domain.OrderDetail;
import org.sejong.jpajoinmaestro.domain.Orders;
import org.sejong.jpajoinmaestro.domain.Product;
import org.sejong.jpajoinmaestro.domain.User;

@Getter
@Setter
public class MyOrder {
    @DTOFieldMapping(domain= User.class, fieldName="username")
    private String username;

    @DTOFieldMapping(domain = Orders.class, fieldName = "status")
    private String status;

    @DTOFieldMapping(domain=Product.class, fieldName="productName")
    private String productName;

    @DTOFieldMapping(domain= OrderDetail.class, fieldName = "quantity")
    private Long quantity;

    @DTOFieldMapping(domain=Product.class, fieldName="price")
    private Long price;

    public MyOrder(){}
}
```
> Note! Be sure to add a default constructor.


### 2. Add `JPAQueryFactory` to your service and use `createJoinQuery`.
If you want to add conditions, declare `ClauseBuilder` and pass it as a parameter to the `createJoinQuery` function.

An example of how to use this is as follows:
```java
@Service
@RequiredArgsConstructor
public class ExampleService {
  private final JPAQueryFactory queryFactory;

  public List<MyOrder> getMyOrder() {
    ClauseBuilder pb = new ClauseBuilder()
      .where(new Equal().to(User.class, "id", 1))
      .andWhere(new Equal().to(Orders.class, "user_id", 1))
      .andWhere(new Equal().to(OrderDetail.class, "orders_id", 1));

    List<MyOrder> results = joinQueryBuilder.createJoinQuery(MyOrder.class, pb);

    return results;
  }
  
}
```
> Note: The Class used in the ClauseBuilder example is a predefined Class with the `@Entity` Annotation.

### It's Done! 

## Performance 
<img width="1481" alt="Screenshot 2024-06-05 at 1 21 16 AM" src="https://github.com/2024-capston/JpaJoinMaestro/assets/79124461/069cd4f4-e619-4034-b460-583e725f6f07">
<img width="1484" alt="Screenshot 2024-06-05 at 1 20 51 AM" src="https://github.com/2024-capston/JpaJoinMaestro/assets/79124461/48312550-8248-4742-b3c0-7038da387395">

> Note: Jmeter was used with the number of threads set to 1.
> The performance metrics example above was measured based on a join of 3 tables.

Temporary unofficial reflection module for Ceylon

```java
import fr.epardaud.raclette { ... }

void test(){
    Class<TestType> c = forName<TestType>("fr.epardaud.raclette.test.TestType");
    TestType t = c.newInstance();
    print(c.qualifiedName);
    for(method in c.methods.values.sorted((Method<TestType,Void> x , Method<TestType,Void> y) x.name <=> y.name)){
        printMethod(method);
    }
    for(attribute in c.attributes.values.sorted((Attribute<TestType,Void> x , Attribute<TestType,Void> y) x.name <=> y.name)){
        printAttribute(attribute);
    }
    assertEquals("Invoked with test, 1, 2.3, true, a", c.methods["myMethod"]?.invoke(t, "test", 1, 2.3, true, `a`));
    assertEquals("Hi there", c.methods["s"]?.invoke(t));
    assertEquals(42, c.methods["i"]?.invoke(t));
    assertEquals(3.4, c.methods["f"]?.invoke(t));
    assertEquals(true, c.methods["b"]?.invoke(t));
    assertEquals(`s`, c.methods["c"]?.invoke(t));
    
    assertEquals("foo", c.attributes["as"]?.get(t));
    assertEquals("foo", c.attributes["vs"]?.get(t));
    c.attributes["vs"]?.set(t, "bar");
    assertEquals("bar", c.attributes["vs"]?.get(t));
}
```

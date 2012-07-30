import fr.epardaud.raclette { ... }

void assertEquals(Void expected, Void val){
    if(exists expected){
        if(exists val){
            if(expected != val){
                throw Exception("Assertion failed. Expecting " expected " but got " val "");
            }
        }else{
            throw Exception("Assertion failed. Expecting " expected " but got null");
        }
    }else{
        if(exists val){
            throw Exception("Assertion failed. Expecting null but got " val "");
        }
    }
}

void printMethod<T>(Method<T, Void> method) {
    for(ann in method.annotations){
        process.write(ann.string);
        process.write(" ");
    }
    process.write(method.type.string);
    process.write(" ");
    process.write(method.name);
    process.write("(");
    variable Boolean once := true;
    for(param in method.parameters){
        if(once){
            once := false;
        }else{
            process.write(", ");
        }
        process.write(param.type.string);
        process.write(" ");
        process.write(param.name);
            if(param.variadic){
            process.write("...");
        }
        if(param.defaulted){
            process.write(" = ?");
        }
    }
    process.write(")\n");
}

void printAttribute<T>(Attribute<T, Void> attribute) {
    for(ann in attribute.annotations){
        process.write(ann.string);
        process.write(" ");
    }
    process.write(attribute.type.string);
    process.write(" ");
    process.write(attribute.name);
    process.write("\n");
}

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
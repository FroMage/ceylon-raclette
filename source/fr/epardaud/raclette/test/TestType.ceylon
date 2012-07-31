shared class TestType(String param) {
    shared String as = "foo";
    String nonSharedAttribute = "bar";
    shared variable String vs := "foo";

    shared String myMethod(String s, Integer i, Float f, Boolean b, Character c){
        return "Invoked with " s ", " i ", " f ", " b ", " c "";
    }
    shared String s(){
        return "Hi there";
    }
    shared Integer i(){
        return 42;
    }
    shared Float f(){
        return 3.4;
    }
    shared Boolean b(){
        return true;
    }
    shared Character c(){
        return `s`;
    }
    void nonSharedMethod(){
        print(nonSharedAttribute);
    }
}
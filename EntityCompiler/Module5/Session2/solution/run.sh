ec build Tutorial ec/Space.edl ec/Configuration.edl ec/Units.edl ec/Domains.edl -tp ec/templates
rm -rf classes ; mkdir classes
javac  -d classes src/com/example/model/*.java src/com/example/Main.java
java -cp classes com.example.Main
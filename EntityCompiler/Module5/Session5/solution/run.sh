ec build Tutorial ec/Space.edl ec/Configuration.edl ec/Units.edl ec/Domains.edl -tp ec/templates
rm -rf classes ; mkdir classes
javac  -d classes src/com/example/model/*.java src/com/example/dto/*.java src/com/example/controller/*.java src/com/example/*.java
java -cp classes com.example.Main
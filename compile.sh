rm *.class
javac Map.java
javac Game.java
jar cvmf manifest.mf Game.jar *.class img
rm *.class
mkdir release
mv Game.jar release
cp -r lvl release

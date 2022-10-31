The project structure is based on composite builds for plugin development. 
See https://github.com/cortinico/kotlin-gradle-plugin-template for the used template.


ev3 gradle plugin:
* ~~Depends on application and shadow plugins~~
* ~~Checks for properties["lejos.home"] as String to be set~~ (Not relevant anymore)
* ~~Adds bdeneuter ev3 and jna classes as compileOnly dependency~~
* Checks for available connectivity to ev3
* If idea plugin is available, adds an IntelliJ remote debug run configuration -> ?
* ~~create ev3AppJarDependencies task~~
* ~~create ev3AppJar task~~
* create ev3Deploy task -> requires configuration: ip, username, password or keyfile, target directory
* create ev3DeployDependencies task -> requires configuration: ip, username, password or keyfile, target directory
* create ev3Run task -> runs the jar over ssh, preferably with tty attached
* create ev3RunDebug task -> runs the jar over ssh, connecting to debug port, preferably with tty attached

Example Ssh commands to run:
Port forward for debugging:
ssh -n -T -L 5005:localhost:5005 root@ev2_usb

Run the app:
ssh -t root@ev2_usb 'jrun -cp /home/lejos/programs/gradletest-1.0.0-app.jar com.legoethals.ev3.gradle.AppKt'

#Previous deploy commands:  
`./gradlew splitShadowJar`
`scp build/libs/gradletest-1.0.0-app.jar root@ev2_usb:/home/lejos/programs`  
`ssh root@ev2_usb 'mkdir -p /home/lejos/programs/libs'`  
`scp build/libs/gradletest-1.0.0-dependencies.jar root@ev2_usb:/home/lejos/programs/libs`  
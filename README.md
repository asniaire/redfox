# RedFox - v1.0

## Overview
This project scans and downloads all the images included in the url provided as parameter, checks if the image is a valid one (png or jpeg) and stores for each image the following fields in our database:
> - **uuid** (unique identifier)
> - **url**	(original url of the image)
> - **path** (local path where the image has been downloaded)
> - **creation_ts** (date where the image is stored)
> - **hash** (sha256 hash of the image)
> - **image_type** (format of the image stored)

The application avoid storing duplicated images according to the **hash**, **url** or **uuid** fields.

The project accepts the following as parameters (it can be showed using **-h** parameter):
> **usage: redfox**
```
 -fs,--full-search    if the execution has to keep finding images recursively - default: false
 -h,--help            show the command line help
 -l,--levels <arg>    how many levels has to keep finding images
                      recursively - default: 1 (except if full-search =
                      true)
 -p,--path <arg>      local path to store the images (required)
 -t,--threads <arg>   number of threads (default = 4)
 -u,--url <arg>       url to scan (required)
```

Only **url** and **path** are mandatory parameters.

Notice that the threads used by the application can be set as parameter. Otherwise, the application will use 4 threads.

The **levels** parameter allows to keep searching images following links as many levels as the indicated by the parameter. The application just follows those links that belongs to the same domain as the original url.

### Output
The application collects some stats during its execution and they are displayed when it ends, along with the time the execution took.

> java -jar redfox-1.0.jar -u http://www.example.es -p /tmp/imagefinder -l 1 -t 6

Output:
```
Scanning...
6 thread used
Scanned images: 176
Processed images: 170
Already processed images: 2
Duplicated images: 1
Invalid image url: 2
Invalid image format: 3
Errors processing image: 0
Scanned links: 10
Followed links: 5
Duplicated links: 5

Finished. Search took 3489 milliseconds
```

## Considerations
This project has been developed using **IntelliJ** as IDE and **Maven** as software project manager, for compilation and dependency management purpose.

The application uses a logger (*SLF4J*) configurable by **logback.xml** file in **src/resources/** directory. The current configuration just push the logger output to **/tmp/redfox/logs/redfox.log**.

Notice that some tests has been included in the project (using **JUnit** and **Mockito**). They can be run, for instance, through maven lifecycle. Please, run:

> mvn clean install

in order to compile, pass tests and build the **jar** file. After that, a **target** folder is created, where you can find the **redfox** JAR file.

## Before running
This project makes use of **mysql** as database server to store the processed images. So, mysql server needs to be running before launching the **image-finder** application. In the **conf** directory you can find a file named **docker** contaning the **docker run** command that start a mysql:5.6 instance automatically (root password = **root**). Otherwise, you can get it running on whatever way you prefer.

After getting mysql up and running we need to execute the **db-initializer.sql** script provided in **conf** directory.

> mysql -u root -p -h 127.0.0.1 &lt; db-initializer.sql

As the application make use of **JPA** (Hibernate) as *ORM* for managing database connections, I have set it up in order to create automatically the **image** table the first time the application is run.

Also, I'm using *c3p0* as **jdbc** connection pooling library.

By default, the application expects to find a mysql server running with the following configuration:
> - **username**: redfox
> - **password**: redfox
> - **host**: 127.0.0.1:3306
> - **database**: redfox

If you prefer another configuration, you need to modify them in **persistence.xml** file (**src/resources/META-INF**) and the aforementioned **db-initializer.sql** file.

Also, if you want to modify the connection pool parameters, you can do that from **persistence.xml**.

## Requirements:
```
- maven 3
- java 8
- mysql 5.6
```

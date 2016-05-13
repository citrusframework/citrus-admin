## Project settings

Each Citrus project has properties and settings that influence the administration UI. These properties are project names, descriptions, versions and source folders.
You can review and change these project related settings with an HTML form on the project settings page.

![Settings](screenshots/project-settings.png)

Some project settings are read only at the moment, e.g. we do not support renaming of projects yet. If you want to rename a project or change the project version you need to do this manually
in the Maven POM configuration.

If you save the project settings the administration UI will save the changes to the project settings file **citrus-project.json** which is located in your project home directory. This file uses the JSON syntax and looks like follows:
 
 ```
 {
   "projectHome" : "~/Projects/Citrus/citrus-sample",
   "name" : "citrus-sample-project",
   "description" : "",
   "version" : "1.0.0",
   "testCount" : 0,
   "settings" : {
     "basePackage" : "com.consol.citrus",
     "citrusVersion" : "2.6",
     "springApplicationContext" : "src/it/resources/citrus-context.xml",
     "javaSrcDirectory" : "src/it/java/",
     "xmlSrcDirectory" : "src/it/resources/",
     "javaFilePattern" : "/**/*Test.java,/**/*IT.java",
     "xmlFilePattern" : "/**/*Test.xml,/**/*IT.xml",
     "build" : {
       "@class" : "com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration",
       "type" : "maven",
       "properties" : [ ],
       "testPlugin" : "maven-failsafe-plugin",
       "profiles" : null
     }
   }
 }
 ```
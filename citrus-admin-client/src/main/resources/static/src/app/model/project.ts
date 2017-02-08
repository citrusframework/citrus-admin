import {Property} from "./property";
import {BuildProperty} from "./build.property";

export class Project {

    constructor() {}

    public name: string;
    public version: string;
    public description: string;

    public settings: ProjectSettings = new ProjectSettings();
}

export class ProjectSettings {

    constructor() {}

    public springApplicationContext: string;
    public javaSrcDirectory: string;
    public xmlSrcDirectory: string;
    public javaFilePattern: string;
    public xmlFilePattern: string;
    public basePackage: string;
    public citrusVersion: string;
    public useConnector: boolean;
    public connectorActive: boolean;
    public tabSize: number;
    public build: BuildConfiguration = new BuildConfiguration();
}

export class BuildConfiguration {

    constructor() {}

    public type: string;
    public command: string;
    public useClean: boolean;
    public profiles: string;
    public properties: BuildProperty[];
}
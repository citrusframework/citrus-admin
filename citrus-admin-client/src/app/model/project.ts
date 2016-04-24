import {Property} from "./property";

export class Project {

    constructor() {}

    public name: string;
    public projectHome: string;
    public version: string;
    public description: string;
    public testCount: number;

    public settings: ProjectSettings = new ProjectSettings();
}

export class ProjectSettings {

    constructor() {}

    public srcDirectory: string;
    public basePackage: string;
    public citrusVersion: string;
    public build: BuildConfiguration = new BuildConfiguration();
}

export class BuildConfiguration {

    constructor() {}

    public type: string;
    public properties: {};
}
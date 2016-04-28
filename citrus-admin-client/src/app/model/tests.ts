import {Property} from "./property";

export class TestPackage {

    constructor() {
        this.tests = [];
    }

    public name: string;
    public tests: Test[];
}

export class Test {

    constructor(name?: string) {
        this.name = name;
    }

    public name: string;
    public className: string;
    public methodname: string;
    public type: string;
    public packageName: string;
}

export class TestDetail extends Test {

    constructor(public name?: string) {
        super(name);
        this.actions = [];
    }

    public groups: string;
    public file: string;

    public lastModified: number;
    public author: string;
    public description: string;

    public variables: any[];
    public parameters: any[];

    public actions: TestAction[];
}

export class TestAction {

    constructor() {
        this.properties = [];
    }

    public type: string;
    public properties: Property[];
}
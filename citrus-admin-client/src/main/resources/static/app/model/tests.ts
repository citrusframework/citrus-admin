import {Property} from "./property";

export class TestGroup {

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
    public methodName: string;
    public methodNames: string[];
    public type: string;
    public packageName: string;
    public relativePath: string;
}

export class TestDetail extends Test {

    constructor(name?: string) {
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

export class TestResult {

    public test: Test;
    public success: boolean;
    public stackTrace: string;
    public failureStack: string;

    public processId: string;
}
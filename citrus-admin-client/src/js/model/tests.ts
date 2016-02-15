export class TestPackage {

    constructor() {
        this.tests = [];
    }

    public name: string;
    public tests: Test[];
}

export class Test {

    constructor(public name?: string) {}

    type: string;
    packageName: string;
}
export class ValidationMatcherLibrary {

    constructor() {
        this.matchers = [];
    }

    public id: string;
    public prefix: string;
    public matchers: ValidationMatcher[];
}

export class ValidationMatcher {

    constructor() { }

    public name: string;
    public ref: string;
    public clazz: string;
}
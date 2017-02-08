export class FunctionLibrary {

    constructor() {
        this.functions = [];
    }

    public id: string;
    public prefix: string;
    public functions: Function[];
}

export class Function {

    constructor() { }

    public name: string;
    public ref: string;
    public clazz: string;
}
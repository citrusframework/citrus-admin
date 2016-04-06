export class NamespaceContext {

    constructor() {
        this.namespaces = [];
    }

    public namespaces: Namespace[];
}

export class Namespace {

    constructor() { }

    public prefix: string;
    public uri: string;
}
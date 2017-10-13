export class DataDictionary {

    constructor() {
        this.mappings = new Mappings;
    }

    public id: string;
    public globalScope: boolean;
    public mappingStrategy: string;
    public mappings: Mappings;
}

export class Mappings {

    constructor() {
        this.mappings = [];
    }

    public mappings: Mapping[];
}

export class Mapping {

    constructor() { }

    public path: string;
    public value: string;
}
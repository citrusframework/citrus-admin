import {Property} from "./property";

export class Endpoint {

    constructor() {
        this.properties = [];
    }

    public id: string;
    public type: string;
    public properties: Property[];
}
import {SpringProperty} from "./springproperty";

export class SpringBean {

    constructor() {
        this.properties = [];
    }

    public properties: SpringProperty[];

    public name: string;
    public clazz: string;
}
export class Alert {

    constructor(type: string,
                message: string,
                autoClear: boolean) {
        this.type = type;
        this.message = message;
        this.autoClear = autoClear;
    }

    public type: string;
    public message: string;
    public autoClear = false;
}
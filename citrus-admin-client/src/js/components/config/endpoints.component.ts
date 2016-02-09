import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {Endpoint} from "../../model/endpoint";
import {Property} from "../../model/property";

@Component({
    selector: 'endpoints',
    templateUrl: 'templates/config/endpoints.html'
})
export class EndpointsComponent implements OnInit {

    constructor(private _configService: ConfigService) {
        this.endpoints = [];
    }

    errorMessage: string;
    endpoint: Endpoint;
    endpoints: Endpoint[];

    endpointTypes = ["jms", "channel", "http-client", "http-server",
                    "ws-client", "ws-server", "websocket-client", "websocket-server",
                    "ssh-client", "ssh-server", "camel", "vertx", "docker",
                    "rmi-client", "rmi-server", "jmx-client", "jmx-server",
                    "mail-client", "mail-server", "ftp-client", "ftp-server"];

    ngOnInit() {
        this.getEndpoints();
    }

    getEndpoints() {
        this._configService.getBeans("endpoints")
            .subscribe(
                endpoints => this.endpoints = endpoints,
                error => this.errorMessage = <any>error);
    }

    selectEndpoint(endpoint: Endpoint) {
        this.endpoint = endpoint;
    }

    newEndpoint(type: string) {
        this._configService.getEndpointType(type)
            .subscribe(
                endpoint => this.endpoint = endpoint,
                error => this.errorMessage = <any>error);
    }

    removeEndpoint(endpoint: Endpoint, event) {
        this._configService.deleteBean(endpoint.id)
            .subscribe(
                response => this.endpoints.splice(this.endpoints.indexOf(endpoint), 1),
                error => this.errorMessage = <any>error);

        event.stopPropagation();
        return false;
    }

    createEndpoint() {
        this._configService.createBean(this.endpoint)
            .subscribe(
                response => console.log(response),
                error => this.errorMessage = <any>error);
    }

    saveEndpoint() {
        this._configService.updateBean(this.endpoint)
            .subscribe(
                response => console.log(response),
                error => this.errorMessage = <any>error);
    }

    cancel() {
        this.endpoint = undefined;
    }
}
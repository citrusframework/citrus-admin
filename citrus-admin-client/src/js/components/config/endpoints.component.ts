import {Component, OnInit} from 'angular2/core';
import {EndpointService} from '../../service/endpoint.service';
import {Endpoint} from "../../model/endpoint";
import {Property} from "../../model/property";

@Component({
    selector: 'endpoints',
    templateUrl: 'templates/config/endpoints.html',
    providers: [EndpointService]
})
export class EndpointsComponent implements OnInit {

    constructor(private _endpointService: EndpointService) {
        this.endpoints = [];
        this.endpointTypes = [];
    }

    errorMessage: string;
    newEndpoint: Endpoint;
    selectedEndpoint: Endpoint;
    endpoints: Endpoint[];

    endpointTypes: string[];

    ngOnInit() {
        this.getEndpointTypes();
        this.getEndpoints();
    }

    getEndpointTypes() {
        this._endpointService.getEndpointTypes()
            .subscribe(
                types => this.endpointTypes = types,
                error => this.errorMessage = <any>error);
    }

    getEndpoints() {
        this._endpointService.getEndpoints()
            .subscribe(
                endpoints => this.endpoints = endpoints,
                error => this.errorMessage = <any>error);
    }

    selectEndpoint(endpoint: Endpoint) {
        this.selectedEndpoint = endpoint;
    }

    getEndpointType(type: string) {
        this._endpointService.getEndpointType(type)
            .subscribe(
                endpoint => this.newEndpoint = endpoint,
                error => this.errorMessage = <any>error);
    }

    removeEndpoint(endpoint: Endpoint, event) {
        this._endpointService.deleteEndpoint(endpoint.id)
            .subscribe(
                response => this.endpoints.splice(this.endpoints.indexOf(endpoint), 1),
                error => this.errorMessage = <any>error);

        event.stopPropagation();
        return false;
    }

    createEndpoint() {
        this._endpointService.createEndpoint(this.newEndpoint)
            .subscribe(
                response => { this.endpoints.push(this.newEndpoint); this.newEndpoint = undefined; },
                error => this.errorMessage = <any>error);
    }

    saveEndpoint() {
        this._endpointService.updateEndpoint(this.selectedEndpoint)
            .subscribe(
                response => { this.selectedEndpoint = undefined },
                error => this.errorMessage = <any>error);
    }

    cancel() {
        if (this.selectedEndpoint) {
            this.selectedEndpoint = undefined;
            this.getEndpoints();
        } else {
            this.newEndpoint = undefined;
        }
    }
}
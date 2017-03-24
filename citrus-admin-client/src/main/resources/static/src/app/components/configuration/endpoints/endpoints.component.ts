import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {EndpointService} from '../../../service/endpoint.service';
import {SpringBeanService} from "../../../service/springbean.service";
import {Endpoint} from "../../../model/endpoint";
import {EndPointStateService, EndPointActions} from "./endpoint.state";
import {Observable} from "rxjs";

@Component({
    selector:'endpoints',
    template: `
      <endpoints-presentation
        [endpoints]="endpoints|async"
        [endpointTypes]="endpointTypeNames|async"
        (removeEndpoint)="removeEndpoint($event)"
      ></endpoints-presentation>
    `
})
export class EndpointsComponent implements OnInit {
    endpoints: Observable<Endpoint[]>;
    endpointTypeNames: Observable<string[]>;

    constructor(
        private endpointState:EndPointStateService,
        private endpointActions:EndPointActions,
    ) {}

    ngOnInit(): void {
        this.endpointActions.fetchEndpointTypes();
        this.endpointActions.fetchEndpoints();
        this.endpoints = this.endpointState.endpoints;
        this.endpointTypeNames = this.endpointState.endpointTypeNames;
    }

    removeEndpoint(endpoint:Endpoint) {
        this.endpointActions.deleteEndpoint(endpoint.id);
    }
}

@Component({
    selector: 'endpoints-presentation',
    templateUrl: 'endpoints.html',
    providers: [EndpointService, SpringBeanService]
})
export class EndpointsPresentationComponent implements OnInit {

    constructor() {}

    @Input() endpoints: Endpoint[];
    @Input() endpointTypes: string[];

    @Output() removeEndpoint = new EventEmitter<Endpoint>();

    ngOnInit() {
    }


    invokeRemove(endpoint: Endpoint, event:MouseEvent) {
        this.removeEndpoint.next(endpoint)
        return false;
    }

}
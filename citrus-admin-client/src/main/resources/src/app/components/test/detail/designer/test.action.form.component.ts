import {Component, Input, OnInit, Output, EventEmitter, OnChanges, SimpleChanges} from "@angular/core";
import {Observable} from "rxjs";
import {Property} from "../../../../model/property";
import {SpringBeanService} from "../../../../service/springbean.service";
import {FormBuilder, FormGroup, Validators, ValidatorFn, AsyncValidatorFn} from "@angular/forms";
import {IdMap} from "../../../../util/redux.util";
import {RestrictAsyncValues} from "../../../util/autocomplete";
import {TestAction} from "../../../../model/tests";
import {EndpointActions, EndpointStateService} from "../../../configuration/endpoints/endpoint.state";
import {Endpoint} from "../../../../model/endpoint";
import * as prettyData from 'pretty-data'

@Component({
    selector: 'test-action-form',
    templateUrl: 'test-action-form.html'
})
export class TestActionFormComponent implements OnChanges {
    @Input() action: TestAction;

    @Output() saved = new EventEmitter<TestAction>();

    form:FormGroup;

    header: Property = new Property();
    beans:{[propKey:string]:Observable<string[]>} = {};

    constructor(
        private fb:FormBuilder,
        private springBeanService:SpringBeanService,
        private endpointState:EndpointStateService,
        private endpointActions:EndpointActions
    ) {
        this.endpointActions.fetchEndpoints();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.beans = this.action.properties
            .filter(p => p.optionType)
            .reduce((beans, p) => ({...beans, [p.optionType]:this.springBeanService.searchBeans(p.optionType)}), {});

        this.form = this.fb.group({
            ...this.action.properties
                .reduce((fg,p) => ({...fg, [p.name]: [p.value, ...this.getValidators(p)]}), {} as IdMap<any>)
        });

        this.properties('message', 'body').forEach(property => {
            property.value = this.prettyPrint(property.value);
        });

        this.header = new Property();
    }

    prettyPrint(value: string): string {
        if (value) {
            let type;
            let messageType = this.property('message.type');
            if (messageType && messageType.value) {
                type = messageType.value;
            }

            if (type == 'xml' || type == 'xhtml') {
                return prettyData.pd.xml(value);
            } else if (type == 'json') {
                return prettyData.pd.json(value);
            }
        }

        return value;
    }

    getProperties(): Property[] {
        return this.action.properties.filter(property => {
            return property.name != 'description'
                && property.name != 'endpoint'
                && property.name != 'message'
                && property.name != 'body'
                && property.name != 'header'
                && property.name != 'headers';
        })
    }

    property(... names: string[]): Property {
        return this.action.properties.find(property => names.indexOf(property.name) > -1);
    }

    properties(... names: string[]): Property[] {
        return this.action.properties.filter(property => names.indexOf(property.name) > -1);
    }

    headers(): Property[] {
        let property = this.property('header', 'headers');

        if (property && property.value) {
            return property.value.split(',').map(entry => {
                let p = new Property();
                let nameValue = entry.split('=');
                p.name = nameValue[0];
                p.value = nameValue[1];
                return p;
            });
        } else {
            return [];
        }
    }

    addHeader() {
        let property = this.property('header', 'headers');
        if (property) {
            if (property.value) {
                property.value += ',' + this.header.name + '=' + this.header.value;
            } else {
                property.value = this.header.name + '=' + this.header.value;
            }
            this.action.dirty = true;
        }

        this.header = new Property();
    }

    removeHeader(toRemove: Property) {
        let property = this.property('header', 'headers');
        if (property) {
            property.value = this.headers().filter(header => header.name != toRemove.name).map(header => header.name + '=' + header.value).join(',');
            this.action.dirty = true;
        }
    }

    getEndpoints(): Observable<Endpoint[]> {
        return this.endpointState.endpoints;
    }

    private getValidators(p: Property):[ValidatorFn, AsyncValidatorFn] {
        const validators:ValidatorFn[] = [(c) => null];
        const asyncValidators:AsyncValidatorFn[] = [];
        if (p.required) {
            validators.push(Validators.required)
        }

        if (p.optionType) {
            asyncValidators.push(RestrictAsyncValues(this.beans[p.optionType]))
        }
        return [
            Validators.compose(validators),
            Validators.composeAsync(asyncValidators)
        ];
    }

    save() {
        this.saved.next(this.action);
    }
}

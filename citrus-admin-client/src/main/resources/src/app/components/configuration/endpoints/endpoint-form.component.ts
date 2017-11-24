import {Component, Input, OnInit, Output, EventEmitter} from "@angular/core";
import {Endpoint} from "../../../model/endpoint";
import {ActivatedRoute, Router} from "@angular/router";
import {EndpointStateService, EndpointActions} from "./endpoint.state";
import {Observable} from "rxjs";
import {EditorMode, EditorDataTupel} from "../editor-mode.enum";
import {Property} from "../../../model/property";
import {SpringBeanService} from "../../../service/springbean.service";
import {FormBuilder, FormGroup, Validators, ValidatorFn, AsyncValidatorFn} from "@angular/forms";
import {IdMap, notNull} from "../../../util/redux.util";
import {RestrictAsyncValues} from "../../util/autocomplete";

@Component({
    selector: 'endpoint-form',
    template: `
        <endpoint-form-presentation
          *ngIf="endpoint|async"
          [endpoint]="endpoint|async"
          [mode]="mode|async"
          (save)="save($event)"
          (cancel)="cancel($event)">
        </endpoint-form-presentation>
    `
})
export class EndpointFormComponent implements OnInit{
    endpoint:Observable<Endpoint>;
    mode:Observable<EditorMode>;
    constructor(
        private route:ActivatedRoute,
        private router:Router,
        private endpointState:EndpointStateService,
        private endpointActions:EndpointActions
    ) {
    }

    ngOnInit(): void {
        this.endpoint = this.route.params
            .switchMap(({name}:{name:string}) => this.endpointState.getEndpoint(name))
            .switchMap(e => e ? Observable.of(e) : this.route
                    .params
                    .filter(({type}) => type != null).first()
                    .do(({type}) => this.endpointActions.fetchEndpointType(type))
                    .switchMap(({type}) => this.endpointState.getEndpointType(type)
                    .filter(notNull()))
            ).filter(notNull()).first();
        this.mode = this.endpoint.map(e => e.id == null ? EditorMode.NEW : EditorMode.EDIT)
    }

    save([mode, endpoint]:EditorDataTupel<Endpoint>) {
        if (EditorMode.NEW === mode) {
            this.endpointActions.createEndpoint(endpoint);
        }

        if (EditorMode.EDIT === mode) {
            this.endpointActions.updateEndpoint(endpoint);
        }

        this.endpointState.getEndpoint(endpoint.id).first().subscribe(e => {
            this.router.navigate(['configuration/endpoints'])
        });
    }

    cancel() {
        this.router.navigate(['configuration/endpoints']);
    }

}

@Component({
    selector: 'endpoint-form-presentation',
    templateUrl: 'endpoint-form.html',
    styles: [``]
})
export class EndpointFormPresentationComponent implements OnInit{
    @Input() endpoint:Endpoint;
    @Input() mode:EditorMode;

    @Output() save = new EventEmitter<EditorDataTupel<Endpoint>>();
    @Output() cancel = new EventEmitter<any>();

    form:FormGroup;

    beans:{[propKey:string]:Observable<string[]>} = {};

    constructor(
        private fb:FormBuilder,
        private springBeanService:SpringBeanService
    ) {
    }

    get isNew() { return this.mode === EditorMode.NEW }

    ngOnInit() {
        this.beans = this.endpoint.properties
            .filter(p => p.optionType)
            .reduce((beans, p) => ({...beans, [p.optionType]:this.springBeanService.searchBeans(p.optionType)}), {});
        this.form = this.fb.group({
            type: [this.endpoint.type],
            id: [this.endpoint.id, Validators.required],
            ...this.endpoint.properties
                .reduce((fg,p) => ({...fg, [p.name]: [p.value, ...this.getValidators(p)]}), {} as IdMap<any>)
        })
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

    invokeSave(endpoint:Endpoint) {
        this.save.next([this.mode, endpoint]);
    }

    invokeCancel() {
        this.cancel.next();
    }

}

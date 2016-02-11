import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {NamespaceContext, Namespace} from "../../model/namespace.context";

@Component({
    selector: 'namespace-context',
    templateUrl: 'templates/config/namespace-context.html',
    providers: [ConfigService]
})
export class NamespaceContextComponent implements OnInit {

    constructor(private _configService: ConfigService) {
        this.namespaceContext = new NamespaceContext();
        this.newNamespace = new Namespace();
    }

    errorMessage: string;
    newNamespace: Namespace;
    namespaceContext: NamespaceContext;

    ngOnInit() {
        this.getNamespaces();
    }

    getNamespaces() {
        this._configService.getNamespaceContext()
            .subscribe(
                namespaceContext => this.namespaceContext = namespaceContext,
                error => this.errorMessage = <any>error);
    }

    addNamespace() {
        this.namespaceContext.namespaces.push(this.newNamespace);

        this._configService.updateNamespaceContext(this.namespaceContext)
            .subscribe(
                response => { this.newNamespace = new Namespace() },
                error => this.errorMessage = <any>error);
    }

    removeNamespace(variable: Namespace, event) {
        this.namespaceContext.namespaces.splice(this.namespaceContext.namespaces.indexOf(variable), 1);

        this._configService.updateNamespaceContext(this.namespaceContext)
            .subscribe(
                response => { this.newNamespace = new Namespace() },
                error => this.errorMessage = <any>error);

        event.stopPropagation();
        return false;
    }

    cancel() {
        this.newNamespace = new Namespace();
    }
}
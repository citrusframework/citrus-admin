import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../service/config.service';
import {NamespaceContext, Namespace} from "../../model/namespace.context";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";

@Component({
    selector: 'namespace-context',
    templateUrl: 'namespace-context.html',
    providers: [ConfigService]
})
export class NamespaceContextComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private _alertService: AlertService) {
        this.namespaceContext = new NamespaceContext();
        this.newNamespace = new Namespace();
    }

    newNamespace: Namespace;
    namespaceContext: NamespaceContext;

    ngOnInit() {
        this.getNamespaces();
    }

    getNamespaces() {
        this._configService.getNamespaceContext()
            .subscribe(
                namespaceContext => this.namespaceContext = namespaceContext,
                error => this.notifyError(<any>error));
    }

    addNamespace() {
        this.namespaceContext.namespaces.push(this.newNamespace);

        this._configService.updateNamespaceContext(this.namespaceContext)
            .subscribe(
                response => {
                    this.notifySuccess("Created namespace '" + this.newNamespace.uri + "'");
                    this.newNamespace = new Namespace();
                },
                error => this.notifyError(<any>error));
    }

    removeNamespace(namespace: Namespace, event:MouseEvent) {
        this.namespaceContext.namespaces.splice(this.namespaceContext.namespaces.indexOf(namespace), 1);

        this._configService.updateNamespaceContext(this.namespaceContext)
            .subscribe(
                response => {
                    this.newNamespace = new Namespace();
                    this.notifySuccess("Removed namespace '" + namespace.uri + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    cancel() {
        this.newNamespace = new Namespace();
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}
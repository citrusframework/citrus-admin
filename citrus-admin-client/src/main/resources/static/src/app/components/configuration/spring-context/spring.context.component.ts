import {Component, OnInit, AfterViewInit} from '@angular/core';
import {SpringBeanService} from "../../../service/springbean.service";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {SpringContext} from "../../../model/springcontext";

declare var jQuery:any;

@Component({
    selector: 'spring-context',
    templateUrl: 'spring-context.html',
    providers: [SpringBeanService]
})
export class SpringContextComponent implements OnInit {

    constructor(private _springBeanService: SpringBeanService,
                private _alertService: AlertService) {
        this.contexts = [];
    }

    selectedContext: SpringContext;
    contexts: SpringContext[];

    editor: any;

    ngOnInit() {
        this.getContexts();
    }

    getContexts() {
        this._springBeanService.getContexts()
            .subscribe(
                contexts => this.contexts = contexts,
                error => this.notifyError(<any>error));
    }

    select(context: SpringContext) {
        this.selectedContext = context;
        this._springBeanService.getContextSource(context)
            .subscribe(
                content => {
                    this.selectedContext.source = content;
                    
                    this.editor = ace.edit("context-editor");
                    this.editor.setTheme("ace/theme/chrome");
                    this.editor.session.setMode("ace/mode/xml");

                    this.editor.setValue(content, 1);
                    this.editor.resize();
                },
                error => this.notifyError(<any>error));
    }

    save() {
        this._springBeanService.updateContextSource(this.selectedContext, this.editor.getValue())
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved context file '" + this.selectedContext.name + "'");
                    this.selectedContext = undefined;
                },
                error => this.notifyError(<any>error));
    }

    cancel() {
        if (this.selectedContext) {
            this.selectedContext = undefined;
            this.getContexts();
        }
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}
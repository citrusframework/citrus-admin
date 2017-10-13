import {Component, OnInit} from '@angular/core';
import {SpringBeanService} from "../../../service/springbean.service";
import {SpringBean} from "../../../model/springbean";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {SpringProperty} from "../../../model/springproperty";
import {FormGroup, FormBuilder, Validators} from "@angular/forms";
import {EditorMode} from "../editor-mode.enum";

declare var jQuery:any;

@Component({
    selector: 'spring-beans',
    templateUrl: 'spring-beans.html',
    providers: [SpringBeanService]
})
export class SpringBeansComponent implements OnInit {

    constructor(
        private _springBeanService: SpringBeanService,
        private _alertService: AlertService,
        private fb:FormBuilder
    ) {
        this.beans = [];
    }

    display = "table";

    bean: SpringBean;
    beans: SpringBean[];

    propertyName: string;
    propertyValue: string;
    form:FormGroup;
    mode:EditorMode;
    EditorMode = EditorMode;

    ngOnInit() {
        this.getSpringBeans();

    }

    private createForm() {
        this.form = this.fb.group({
            type: [this.bean.clazz, Validators.required],
            name: [this.bean.id, Validators.required],
        })
    }

    private reset() {
        this.form = null;
        this.bean = null;
        this.mode = null;
    }

    getSpringBeans() {
        this._springBeanService.getAllBeans()
            .subscribe(
                beans => this.beans = beans,
                error => this.notifyError(<any>error));
    }

    initNew() {
        this.bean = new SpringBean();
        this.mode = EditorMode.NEW;
        this.createForm();
    }

    selectBean(bean: SpringBean) {
        this.bean = bean;
        this.mode = EditorMode.EDIT;
        this.createForm();
    }

    removeBean(bean: SpringBean, event:MouseEvent) {
        this._springBeanService.deleteBean(bean)
            .subscribe(
                response => {
                    this.beans.splice(this.beans.indexOf(bean), 1);
                    this.notifySuccess("Removed bean '" + bean.id + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    submit() {
        if(EditorMode.NEW === this.mode) {
            this.createBean();
        }
        if(EditorMode.EDIT === this.mode) {
            this.saveBean();
        }
    }

    createBean() {
        this._springBeanService.createBean(this.bean)
            .subscribe(
                response => {
                    this.notifySuccess("Created new bean '" + this.bean.id + "'");
                    this.beans.push(this.bean); this.bean = undefined;
                    this.reset();
                },
                error => this.notifyError(<any>error));
    }

    saveBean() {
        this._springBeanService.updateBean(this.bean)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved bean '" + this.bean.id + "'");
                    this.reset();
                },
                error => this.notifyError(<any>error));
    }

    addProperty(bean: SpringBean) {
        var property = new SpringProperty();
        property.name = this.propertyName;
        property.value = this.propertyValue;
        bean.properties.push(property);

        this.propertyName = "";
        this.propertyValue = "";

    }

    removeProperty(bean: SpringBean, property: SpringProperty, event:MouseEvent) {
        bean.properties.splice(bean.properties.indexOf(property), 1);
        event.stopPropagation();
        return false;

    }

    cancel() {
        if (EditorMode.EDIT === this.mode) {
            this.getSpringBeans();
        }
        this.reset();
    }

    setDisplay(type: string) {
        this.display = type;
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", JSON.stringify(error), false));
    }
}
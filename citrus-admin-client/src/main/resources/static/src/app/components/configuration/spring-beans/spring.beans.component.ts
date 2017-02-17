import {Component, OnInit} from '@angular/core';
import {SpringBeanService} from "../../../service/springbean.service";
import {SpringBean} from "../../../model/springbean";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {SpringProperty} from "../../../model/springproperty";

declare var jQuery:any;

@Component({
    selector: 'spring-beans',
    templateUrl: 'spring-beans.html',
    providers: [SpringBeanService]
})
export class SpringBeansComponent implements OnInit {

    constructor(private _springBeanService: SpringBeanService,
                private _alertService: AlertService) {
        this.beans = [];
    }

    newBean: SpringBean;
    selectedBean: SpringBean;
    beans: SpringBean[];

    propertyName: string;
    propertyValue: string;

    ngOnInit() {
        this.getSpringBeans();
    }

    getSpringBeans() {
        this._springBeanService.getAllBeans()
            .subscribe(
                beans => this.beans = beans,
                error => this.notifyError(<any>error));
    }

    initNew() {
        this.newBean = new SpringBean();
    }

    selectBean(bean: SpringBean) {
        this.selectedBean = bean;
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

    createBean() {
        this._springBeanService.createBean(this.newBean)
            .subscribe(
                response => {
                    this.notifySuccess("Created new bean '" + this.newBean.id + "'");
                    this.beans.push(this.newBean); this.newBean = undefined;
                },
                error => this.notifyError(<any>error));
    }

    saveBean() {
        this._springBeanService.updateBean(this.selectedBean)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved bean '" + this.selectedBean.id + "'");
                    this.selectedBean = undefined;
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
        if (this.selectedBean) {
            this.selectedBean = undefined;
            this.getSpringBeans();
        } else {
            this.newBean = undefined;
        }
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}
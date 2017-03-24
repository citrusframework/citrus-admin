import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {SpringBean} from "../model/springbean";
import {SpringContext} from "../model/springcontext";
import {TextHeader, toJson} from "./common";

@Injectable()
export class SpringBeanService {

    constructor (private http: Http) {}

    private serviceUrl = 'api/beans';

    searchBeans(type: string):Observable<string[]> {
        return this.http.post(`${this.serviceUrl}/search`, type, TextHeader)
                        .map(toJson())
                        .catch(this.handleError);
    }

    getContexts() {
        return this.http.get(this.serviceUrl + '/context')
            .map(res => <SpringContext[]> res.json())
            .catch(this.handleError);
    }

    getContextSource(context: SpringContext) {
        return this.http.get(this.serviceUrl + '/context' + '/' + context.fileName + '/')
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    updateContextSource(context: SpringContext, source: string) {
        return this.http.put(this.serviceUrl + '/context/' + context.fileName + '/', source)
            .catch(this.handleError);
    }

    getAllBeans() {
        return this.http.get(this.serviceUrl)
            .map(res => <SpringBean[]> res.json())
            .catch(this.handleError);
    }

    getBeans(type: string) {
        return this.http.get(this.serviceUrl + '/' + type)
                        .map(res => <SpringBean[]> res.json())
                        .catch(this.handleError);
    }

    getBean(type: string, id: string) {
        return this.http.get(this.serviceUrl + '/' + type + '/' + id)
            .map(res => <SpringBean> res.json())
            .catch(this.handleError);
    }

    createBean(bean: SpringBean) {
        return this.http.post(this.serviceUrl, JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
    }

    updateBean(bean: SpringBean) {
        if (bean.id) {
            return this.http.put(this.serviceUrl + '/' + bean.clazz +  '/' + bean.id, JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                    .catch(this.handleError);
        } else {
            return this.http.put(this.serviceUrl + '/' + bean.clazz + '/', JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
        }
    }

    deleteBean(bean: SpringBean) {
        if (bean.id) {
            return this.http.delete(this.serviceUrl + '/' + bean.clazz + '/' + bean.id)
                .catch(this.handleError);
        } else {
            return this.http.delete(this.serviceUrl + '/' + bean.clazz + '/')
                .catch(this.handleError);
        }
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}
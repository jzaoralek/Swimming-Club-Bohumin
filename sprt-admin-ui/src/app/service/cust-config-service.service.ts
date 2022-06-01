import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { CustConfig } from '../model/cust-config';
import { CustConfigResp } from '../model/cust-config-resp';
import { environment } from '../../environments/environment';

@Injectable()
export class CustConfigService {

  private custConfigCreateUrl: string;
  private healthCheckUrl: string;
  private custTargetUrlUrl: string;


  constructor(private http: HttpClient) {
    /*
    this.custConfigCreateUrl = 'https://admin.sportologic.cz/api/cust-config-create';
    this.healthCheckUrl = 'https://admin.sportologic.cz/api/health-check';
    this.custConfigCreateUrl = 'http://localhost:8080/cust-config-create';
    this.healthCheckUrl = 'http://localhost:8080/health-check';
    */
    this.custConfigCreateUrl = environment.custConfigCreateUrl;
    this.healthCheckUrl = environment.healthCheckUrl;
    this.custTargetUrlUrl = environment.custTargetUrlUrl;
  }

  public create(user: CustConfig, recaptchaToken: string) {

    const headers = new HttpHeaders()
    .append(
      'Content-Type',
      'application/json'
    );

    const params = new HttpParams()
      .append('g-recaptcha-response', recaptchaToken);

    return this.http.post<CustConfigResp>(this.custConfigCreateUrl, user, 
                                          {headers: headers,
                                          params: params});
  }

  public healtCheck() {
    return this.http.get(this.healthCheckUrl,{responseType:'text'});
  }

  public getCustTargetUrl(custConfig: CustConfig) {
    return this.http.post<CustConfigResp>(this.custTargetUrlUrl, custConfig);
  }
}

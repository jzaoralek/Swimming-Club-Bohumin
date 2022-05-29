import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CustConfig } from '../model/cust-config';
import { CustConfigResp } from '../model/cust-config-resp';
import { environment } from '../../environments/environment';

@Injectable()
export class CustConfigService {

  private custConfigCreateUrl: string;
  private healthCheckUrl: string;


  constructor(private http: HttpClient) {
    /*
    this.custConfigCreateUrl = 'https://admin.sportologic.cz/api/cust-config-create';
    this.healthCheckUrl = 'https://admin.sportologic.cz/api/health-check';
    this.custConfigCreateUrl = 'http://localhost:8080/cust-config-create';
    this.healthCheckUrl = 'http://localhost:8080/health-check';
    */
    this.custConfigCreateUrl = environment.custConfigCreateUrl;
    this.healthCheckUrl = environment.healthCheckUrl;
  }

  public create(user: CustConfig, recaptchaToken: string) {
    return this.http.post<CustConfigResp>(this.custConfigCreateUrl, {user, recaptchaToken});
  }

  public healtCheck() {
    return this.http.get(this.healthCheckUrl,{responseType:'text'});
  }
}

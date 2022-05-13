import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CustConfig } from '../model/cust-config';
import { CustConfigResp } from '../model/cust-config-resp';



@Injectable()
export class CustConfigService {

  private custConfigCreateUrl: string;

  constructor(private http: HttpClient) {
    this.custConfigCreateUrl = 'http://localhost:8080/cust-config-create';
  }

  public create(user: CustConfig) {
    return this.http.post<CustConfigResp>(this.custConfigCreateUrl, user);
  }
}


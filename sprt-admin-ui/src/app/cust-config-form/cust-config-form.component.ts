import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustConfigService } from '../service/cust-config-service.service';
import { CustConfig } from '../model/cust-config';
import { CustConfigResp } from '../model/cust-config-resp';
import {Observable,of, from } from 'rxjs';
import {HttpResponse} from '@angular/common/http';
import { NgForm } from '@angular/forms';

declare var grecaptcha: any;

@Component({
  selector: 'app-cust-config-form',
  templateUrl: './cust-config-form.component.html',
  styleUrls: ['./cust-config-form.component.css']
})
export class CustConfigFormComponent {

  custConfig: CustConfig;
  validMsg!: String;
  token: string;
  custTargetUrl: string;

  constructor(private route: ActivatedRoute, 
              private router: Router, 
              private custConfigService: CustConfigService) {
    this.custConfig = new CustConfig();
    this.token = "";
    this.custTargetUrl = "";
  }

  onCustNameChange(event: any){
    console.log(event.target.value);
    this.custConfigService.getCustTargetUrl(this.custConfig).subscribe(result => this.custTargetUrlResult(result));
  }

  custTargetUrlResult(result: CustConfigResp) {
    if (result.status == 'OK') {
      this.custTargetUrl = result.custInstanceUrl;
      this.showWarnMsg("");
    } else if (result.status == 'VALIDATION') {
      this.custTargetUrl = "";
      this.showWarnMsg(result.description);
    }
  }

  onSubmit() {

    console.log(`ReCaptcha token [${this.token}] generated`);

    if (this.token.length === 0) {
      this.showWarnMsg('Neplatné ověření reCaptcha. Prosím vyzkoušejte znova!');
      return;
    }

    this.custConfigService.create(this.custConfig, this.token).subscribe(result => this.gotoResult(result));
  }

  gotoResult(data: CustConfigResp) {
    if (data.status == 'OK') {
      // redirect to result
      this.router.navigate(['/cust-config-result'], {
        state: {
          newCustData: JSON.stringify(data)
        }
      });
    } else if (data.status == 'VALIDATION') {
      this.showWarnMsg(data.description);
    }
  }

  public send(form: NgForm): void {
    if (form.invalid) {
      for (const control of Object.keys(form.controls)) {
        form.controls[control].markAsTouched();
      }
      return;
    }    
  }

  private showWarnMsg(msg: string): void {
    this.validMsg = msg;
  }
}

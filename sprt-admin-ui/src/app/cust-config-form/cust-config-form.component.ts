import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustConfigService } from '../service/cust-config-service.service';
import { CustConfig } from '../model/cust-config';
import { CustConfigResp } from '../model/cust-config-resp';

@Component({
  selector: 'app-cust-config-form',
  templateUrl: './cust-config-form.component.html',
  styleUrls: ['./cust-config-form.component.css']
})
export class CustConfigFormComponent {

  custConfig: CustConfig;

  constructor(private route: ActivatedRoute, 
              private router: Router, 
              private custConfigService: CustConfigService) {
    this.custConfig = new CustConfig();
  }

  onSubmit() {
    this.custConfigService.create(this.custConfig).subscribe(result => this.gotoResult(result));
  }

  gotoResult(data: CustConfigResp) {
    this.router.navigate(['/cust-config-result'], {
      state: {
        newCustData: JSON.stringify(data)
      }
    });
  }
}

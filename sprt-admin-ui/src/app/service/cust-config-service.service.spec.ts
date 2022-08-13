import { TestBed } from '@angular/core/testing';

import { CustConfigServiceService } from './cust-config-service.service';

describe('CustConfigServiceService', () => {
  let service: CustConfigServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustConfigServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

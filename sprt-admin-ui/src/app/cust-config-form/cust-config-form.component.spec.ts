import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustConfigFormComponent } from './cust-config-form.component';

describe('CustConfigFormComponent', () => {
  let component: CustConfigFormComponent;
  let fixture: ComponentFixture<CustConfigFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustConfigFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

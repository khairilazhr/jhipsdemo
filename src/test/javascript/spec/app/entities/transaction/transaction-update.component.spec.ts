/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import TransactionUpdateComponent from '@/entities/transaction/transaction-update.vue';
import TransactionClass from '@/entities/transaction/transaction-update.component';
import TransactionService from '@/entities/transaction/transaction.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Transaction Management Update Component', () => {
    let wrapper: Wrapper<TransactionClass>;
    let comp: TransactionClass;
    let transactionServiceStub: SinonStubbedInstance<TransactionService>;

    beforeEach(() => {
      transactionServiceStub = sinon.createStubInstance<TransactionService>(TransactionService);

      wrapper = shallowMount<TransactionClass>(TransactionUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          transactionService: () => transactionServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.transaction = entity;
        transactionServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(transactionServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.transaction = entity;
        transactionServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(transactionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundTransaction = { id: 123 };
        transactionServiceStub.find.resolves(foundTransaction);
        transactionServiceStub.retrieve.resolves([foundTransaction]);

        // WHEN
        comp.beforeRouteEnter({ params: { transactionId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.transaction).toBe(foundTransaction);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});

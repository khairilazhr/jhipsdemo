import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Transaction e2e test', () => {
  let startingEntitiesCount = 0;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });

    cy.clearCookies();
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.visit('');
    cy.login('admin', 'admin');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  it('should load Transactions', () => {
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('Transaction').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details Transaction page', () => {
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('transaction');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create Transaction page', () => {
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Transaction');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit Transaction page', () => {
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('Transaction');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of Transaction', () => {
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Transaction');

    cy.get(`[data-cy="name"]`).type('deposit Rubber', { force: true }).invoke('val').should('match', new RegExp('deposit Rubber'));

    cy.get(`[data-cy="title"]`).type('Circle', { force: true }).invoke('val').should('match', new RegExp('Circle'));

    cy.get(`[data-cy="totalprice"]`).type('27734').should('have.value', '27734');

    cy.get(`[data-cy="date"]`).type('2024-03-10').should('have.value', '2024-03-10');

    cy.get(`[data-cy="gender"]`)
      .type('methodology Seychelles', { force: true })
      .invoke('val')
      .should('match', new RegExp('methodology Seychelles'));

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/api/transactions*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of Transaction', () => {
    cy.intercept('GET', '/api/transactions*').as('entitiesRequest');
    cy.intercept('DELETE', '/api/transactions/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.getEntityDeleteDialogHeading('transaction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/api/transactions*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('transaction');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});

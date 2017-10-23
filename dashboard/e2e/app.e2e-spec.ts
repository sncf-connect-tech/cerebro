import { AppPage } from './app.po';

describe('cerebro-dashboard App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should display title', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Cerebro (beta)');
  });
});

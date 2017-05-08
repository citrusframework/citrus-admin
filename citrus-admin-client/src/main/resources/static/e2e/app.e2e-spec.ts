import { TEMPPage } from './app.po';

describe('temp App', () => {
  let page: TEMPPage;

  beforeEach(() => {
    page = new TEMPPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});

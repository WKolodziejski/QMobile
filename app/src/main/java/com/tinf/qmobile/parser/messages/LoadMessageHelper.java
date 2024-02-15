package com.tinf.qmobile.parser.messages;

import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.network.Client;

import java.util.HashMap;
import java.util.Map;

public final class LoadMessageHelper {

  private LoadMessageHelper() {}

  /* Para carregar a mensagem
          1 - carregar página de mensagens
          2 - retornar campos do form
          3 - para cada página, buscar se ela possui o uid desejado
          4 - a página que tiver o uid deverá retornar os campos do form
          5 - chamar um SingleMessageParser
         */
  public static void loadMessage(Message message) {
    // Carrega página e retorna campos do form
    Client.get()
          .loadMessagesForm((ev, vs, pc) -> onLoadFormPage(ev, vs, pc, message));
  }

  private static void onLoadFormPage(String eventValidation,
                                     String viewState,
                                     int pagesCount,
                                     Message message) {
    // Para cada página em pagesCount carrega a página
    for (int i = 1; i <= pagesCount; i++) {
      Map<String, String> form = new HashMap<>();
      form.put("__EVENTVALIDATION", eventValidation);
      form.put("__VIEWSTATE", viewState);
      form.put("__EVENTARGUMENT", "Page$" + i);
      form.put("__EVENTTARGET", "ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens");

      // Carrega páginas e retorna callback se encontrar a mensagem
      Client.get()
            .findMessagesPage((ev, vs, pc) -> onMessagePageFound(ev, vs, pc, message), form, message, i);
    }
  }

  private static void onMessagePageFound(String eventValidation,
                                         String viewState,
                                         int messagePosition,
                                         Message message) {
    Map<String, String> form = new HashMap<>();
    form.put("__EVENTVALIDATION", eventValidation);
    form.put("__VIEWSTATE", viewState);
    form.put("__EVENTARGUMENT", "exibir_mensagem$" + messagePosition);
    form.put("__EVENTTARGET", "ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens");

    Client.get()
          .load(message, form);
  }
}

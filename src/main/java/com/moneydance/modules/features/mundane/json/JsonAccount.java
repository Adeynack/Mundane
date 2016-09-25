package com.moneydance.modules.features.mundane.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.infinitekind.moneydance.model.Account;

@JsonPropertyOrder({"name"})
public class JsonAccount {

    public String name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public JsonAccount[] subAccounts = {};

    public static JsonAccount fromAccount(Account account) {
        JsonAccount j = new JsonAccount();
        j.name = account.getAccountName();
        j.subAccounts = account.getSubAccounts().stream().map(JsonAccount::fromAccount).toArray(JsonAccount[]::new);
        return j;
    }

}

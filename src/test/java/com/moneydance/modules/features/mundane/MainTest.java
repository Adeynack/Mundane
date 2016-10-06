package com.moneydance.modules.features.mundane;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneydance.modules.features.mundane.json.JsonAccount;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// todo: Find a way to run ScalaTest AND JUnit

public class MainTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testName() throws Exception {
        Main main = new Main();
        assertEquals("Account List", main.getName());
    }

    @Test
    public void accountSerializesDoesNotIncludeSubAccountsWhenEmpty() throws Exception {
        JsonAccount a = new JsonAccount();
        a.name = "foo";
        String json = mapper.writeValueAsString(a);
        assertEquals("{\"name\":\"foo\"}", json);
    }

    @Test
    public void accountSerializesIncludeSubAccountsWhenNonEmpty() throws Exception {
        JsonAccount subAccount1 = new JsonAccount();
        subAccount1.name = "a.1";

        JsonAccount subAccount2SubAccount1 = new JsonAccount();
        subAccount2SubAccount1.name = "a.2.1";

        JsonAccount subAccount2 = new JsonAccount();
        subAccount2.name = "a.2";
        subAccount2.subAccounts = new JsonAccount[]{subAccount2SubAccount1};

        JsonAccount account = new JsonAccount();
        account.name = "a";
        account.subAccounts = new JsonAccount[]{subAccount1, subAccount2};

        String json = mapper.writeValueAsString(account);
        assertEquals("{\"name\":\"a\",\"subAccounts\":[{\"name\":\"a.1\"},{\"name\":\"a.2\",\"subAccounts\":[{\"name\":\"a.2.1\"}]}]}", json);
    }

}

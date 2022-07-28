package gg.nodus.girlboss;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class SignatureTracker {

    private static final Multimap<UUID, byte[]> signatures = MultimapBuilder.hashKeys().hashSetValues().build();

    public static void addSignature(UUID sender, byte[] signature) {
        signatures.put(sender, signature);
    }

    public static Collection<byte[]> getSignatures(UUID sender) {
        return signatures.get(sender);
    }

}

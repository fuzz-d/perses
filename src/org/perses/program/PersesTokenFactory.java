package org.perses.program;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.antlr.v4.runtime.Token;

import static com.google.common.base.Preconditions.checkState;

public final class PersesTokenFactory {

  public static final int DEFAULT_RETURN_VALUE = Integer.MIN_VALUE;

  public static final class Builder {
    private final Object2IntOpenHashMap<String> lexeme2persesTokenIdMap;

    public Builder() {
      this.lexeme2persesTokenIdMap = new Object2IntOpenHashMap(200);
      this.lexeme2persesTokenIdMap.defaultReturnValue(DEFAULT_RETURN_VALUE);
    }

    public PersesToken create(Token token) {
      final String lexeme = token.getText();
      int persesTokenId = lexeme2persesTokenIdMap.getInt(lexeme);
      if (persesTokenId == Integer.MIN_VALUE) {
        persesTokenId = lexeme2persesTokenIdMap.size();
        lexeme2persesTokenIdMap.put(lexeme, persesTokenId);
      }
      return new PersesToken(persesTokenId, token);
    }

    public PersesTokenFactory build() {
      return new PersesTokenFactory(lexeme2persesTokenIdMap);
    }
  }

  private final Object2IntOpenHashMap<String> lexeme2persesTokenIdMap;

  private PersesTokenFactory(Object2IntOpenHashMap<String> lexeme2persesTokenIdMap) {
    this.lexeme2persesTokenIdMap = lexeme2persesTokenIdMap;
  }

  public int numOfLexemes() {
    return lexeme2persesTokenIdMap.size();
  }

  public PersesToken getPersesToken(Token token) {
    final int persesLexemeId =
        lexeme2persesTokenIdMap.getOrDefault(token.getText(), DEFAULT_RETURN_VALUE);
    checkState(persesLexemeId != DEFAULT_RETURN_VALUE);
    return new PersesToken(persesLexemeId, token);
  }
}
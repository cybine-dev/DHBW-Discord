package de.cybine.dhbw.discordbot.api.external;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Optional;

@Data
@Builder(builderClassName = "Builder")
public class PaginationResult<T>
{
    private final int total;

    private final int limit;
    private final int offset;

    private final String next;

    private final Collection<T> items;

    public Optional<String> getNext( )
    {
        return Optional.ofNullable(this.next);
    }

    public boolean hasNext( )
    {
        return this.next != null && this.total > this.limit + this.offset;
    }
}

"""add bout lifecycle and messages

Revision ID: 7a89b01c2d3e
Revises: e5581c3d4f2c
Create Date: 2026-11-24 16:47:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '7a89b01c2d3e'
down_revision: Union[str, Sequence[str], None] = 'e5581c3d4f2c'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Add columns to bouts table
    with op.batch_alter_table('bouts', schema=None) as batch_op:
        batch_op.add_column(sa.Column('venue_name', sa.String(length=150), nullable=True))
        batch_op.add_column(sa.Column('scheduled_at', sa.DateTime(), nullable=True))
        batch_op.add_column(sa.Column('opponent_accepted', sa.Boolean(), server_default='0', nullable=False))
        batch_op.add_column(sa.Column('deposit_amount', sa.Integer(), server_default='150000', nullable=False))
        batch_op.add_column(sa.Column('challenger_deposit_paid', sa.Boolean(), server_default='0', nullable=False))
        batch_op.add_column(sa.Column('opponent_deposit_paid', sa.Boolean(), server_default='0', nullable=False))
        batch_op.add_column(sa.Column('winner_id', sa.Integer(), nullable=True))
        batch_op.add_column(sa.Column('result_method', sa.String(length=50), nullable=True))
        batch_op.add_column(sa.Column('result_round', sa.Integer(), nullable=True))
        batch_op.add_column(sa.Column('result_notes', sa.Text(), nullable=True))
        batch_op.add_column(sa.Column('rating_challenger', sa.Integer(), nullable=True))
        batch_op.add_column(sa.Column('rating_opponent', sa.Integer(), nullable=True))
        batch_op.create_foreign_key('fk_bouts_winner_id', 'fighters', ['winner_id'], ['id'], ondelete='SET NULL')

    # Create messages table
    op.create_table(
        'messages',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('sender_id', sa.Integer(), nullable=False),
        sa.Column('receiver_id', sa.Integer(), nullable=False),
        sa.Column('bout_id', sa.Integer(), nullable=True),
        sa.Column('content', sa.Text(), nullable=False),
        sa.Column('is_read', sa.Boolean(), server_default='0', nullable=False),
        sa.Column('created_at', sa.DateTime(), server_default=sa.text('now()'), nullable=False),
        sa.ForeignKeyConstraint(['sender_id'], ['fighters.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['receiver_id'], ['fighters.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['bout_id'], ['bouts.id'], ondelete='SET NULL'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_messages_sender_id'), 'messages', ['sender_id'], unique=False)
    op.create_index(op.f('ix_messages_receiver_id'), 'messages', ['receiver_id'], unique=False)
    op.create_index(op.f('ix_messages_bout_id'), 'messages', ['bout_id'], unique=False)


def downgrade() -> None:
    op.drop_index(op.f('ix_messages_bout_id'), table_name='messages')
    op.drop_index(op.f('ix_messages_receiver_id'), table_name='messages')
    op.drop_index(op.f('ix_messages_sender_id'), table_name='messages')
    op.drop_table('messages')

    with op.batch_alter_table('bouts', schema=None) as batch_op:
        batch_op.drop_constraint('fk_bouts_winner_id', type_='foreignkey')
        batch_op.drop_column('rating_opponent')
        batch_op.drop_column('rating_challenger')
        batch_op.drop_column('result_notes')
        batch_op.drop_column('result_round')
        batch_op.drop_column('result_method')
        batch_op.drop_column('winner_id')
        batch_op.drop_column('opponent_deposit_paid')
        batch_op.drop_column('challenger_deposit_paid')
        batch_op.drop_column('deposit_amount')
        batch_op.drop_column('opponent_accepted')
        batch_op.drop_column('scheduled_at')
        batch_op.drop_column('venue_name')

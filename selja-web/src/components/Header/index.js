import { Link } from 'react-router-dom'
import { makeStyles } from '@material-ui/core/styles'
import { Typography, Fab, Container } from '@material-ui/core'
import AddIcon from '@material-ui/icons/Add';
import DoubleArrow from '@material-ui/icons/DoubleArrow';
import React from 'react';
import styled from 'styled-components'

const StyledLink = styled(Link)`
    color: white;
    text-decoration: none;
    &:hover {
        color: grey;
    }
`
const useStyles = makeStyles(theme => ({
    root: {
        padding: theme.spacing(2, 2),
        display: 'flex',
        justifyContent: 'space-between'
    },
    icon: {
        width: 55,
        height: 55,
    },
    home: {
        display: 'flex',
    },
    fab: {
        margin: theme.spacing(1),
    },
    addIcon: {
        marginRight: theme.spacing(2),
    },
}))

export default function Header(props) {
    const classes = useStyles()
    return (
        <Typography variant='h3' color="textPrimary" className={classes.root}>
            <Container className={classes.home}>
                <DoubleArrow className={classes.icon} />
                <StyledLink to="/" >Selja</StyledLink>
            </Container>
            <Link style={{ textDecoration: 'none' }} to="/newItem">
                <Fab variant="extended" aria-label="delete" className={classes.fab} >
                    <AddIcon className={classes.addIcon} /> New ad
                </Fab>
            </Link>

        </Typography>
    )
}
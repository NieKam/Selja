import React from 'react';
import { Typography, Link } from '@material-ui/core'
import { makeStyles } from '@material-ui/core/styles'

const useStyles = makeStyles(theme => ({
    footer: {
        textAlign: 'center',
        padding: theme.spacing(4),
    }
}))

export default function Footer() {
    const classes = useStyles()
    const url = "https://github.com/NieKam/Selja"

    return (
        <Typography className={classes.footer} variant="subtitle1" color="textSecondary">Source on <span role="img" aria-label="sheep">💾</span> and <Link color="inherit" href={url}>Github</Link>, 2019</Typography>
    )
}